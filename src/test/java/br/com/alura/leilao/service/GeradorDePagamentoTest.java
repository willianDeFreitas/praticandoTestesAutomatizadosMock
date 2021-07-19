package br.com.alura.leilao.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;

class GeradorDePagamentoTest {

	private GeradorDePagamento service;

	@Mock
	private PagamentoDao pagamentoDao;

	@Captor
	private ArgumentCaptor<Pagamento> captor;

	@Mock
	private Clock clock;

	@BeforeEach
	public void beforeEach() {
		MockitoAnnotations.openMocks(this);
		this.service = new GeradorDePagamento(pagamentoDao, clock);
	}

	@Test
	void deveriaCriarPagamentoParaVencedorLeilao() {
		LocalDate data = LocalDate.of(2021, 7, 19);
		Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();
		Mockito.when(clock.instant()).thenReturn(instant);
		Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

		Leilao leilao = buildLeilao();
		service.gerarPagamento(leilao.getLanceVencedor());
		Mockito.verify(pagamentoDao).salvar(captor.capture());
		Pagamento pagamento = captor.getValue();

		Assert.assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());
		Assert.assertEquals(leilao.getLanceVencedor().getValor(), pagamento.getValor());
		Assert.assertFalse(pagamento.getPago());
		Assert.assertEquals(leilao.getLanceVencedor().getUsuario(), pagamento.getUsuario());
		Assert.assertEquals(leilao, pagamento.getLeilao());
	}

	private Leilao buildLeilao() {
		Leilao leilao = new Leilao("Celular", new BigDecimal("500"), new Usuario("Fulano"));
		Lance lance = new Lance(new Usuario("Ciclano"), new BigDecimal("900"));
		leilao.propoe(lance);
		leilao.setLanceVencedor(lance);
		return leilao;
	}

}
