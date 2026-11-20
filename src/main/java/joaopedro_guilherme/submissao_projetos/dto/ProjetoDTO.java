package joaopedro_guilherme.submissao_projetos.dto;

import java.math.BigDecimal;

public class ProjetoDTO {
    private BigDecimal notaInovacao;
    private BigDecimal notaApresentacao;

    public BigDecimal getNotaInovacao() { return notaInovacao; }
    public void setNotaInovacao(BigDecimal notaInovacao) { this.notaInovacao = notaInovacao; }

    public BigDecimal getNotaApresentacao() { return notaApresentacao; }
    public void setNotaApresentacao(BigDecimal notaApresentacao) { this.notaApresentacao = notaApresentacao; }
}

