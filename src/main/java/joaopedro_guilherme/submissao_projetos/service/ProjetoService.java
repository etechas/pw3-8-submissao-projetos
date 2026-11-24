package joaopedro_guilherme.submissao_projetos.service;

import joaopedro_guilherme.submissao_projetos.enums.TipoStatusEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ProjetoService {

    public BigDecimal media(BigDecimal notaInovacao, BigDecimal notaApresentacao){
        return notaInovacao.add(notaApresentacao)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }

    public TipoStatusEnum aprovar(BigDecimal mediaFinal, TipoStatusEnum status){
        if(mediaFinal.compareTo(BigDecimal.valueOf(7))>= 0){
            status = TipoStatusEnum.APROVADO;
        }else{
            status = TipoStatusEnum.REVISAR;
        }
        //System.out.println(status);
        return status;
    }
    /*Regra de Negócio: Ao enviar as notas, o backend calcula a mediaFinal e atualiza automaticamente
     o status para APROVADO (se mediaFinal >= 7.0) ou REVISAR.*/
}
