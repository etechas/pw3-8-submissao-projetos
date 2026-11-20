package joaopedro_guilherme.submissao_projetos.controller;

import joaopedro_guilherme.submissao_projetos.dto.ProjetoDTO;
import joaopedro_guilherme.submissao_projetos.entity.Projeto;
import joaopedro_guilherme.submissao_projetos.enums.TipoStatusEnum;
import joaopedro_guilherme.submissao_projetos.repository.ProjetoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {
    private static final BigDecimal NOTA_MINIMA = BigDecimal.valueOf(7);

    private final ProjetoRepository repository;

    public ProjetoController(ProjetoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Projeto> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projeto> buscar(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<Projeto> criar(@RequestBody Projeto projeto) {
        projeto.setId(null);
        projeto.setNotaInovacao(null);
        projeto.setNotaApresentacao(null);
        projeto.setMediaFinal(null);
        projeto.setStatus(null);
        Projeto salvo = repository.save(projeto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Projeto> atualizar(@PathVariable Long id, @RequestBody Projeto dados) {
        return repository.findById(id)
                .map(projeto -> {
                    projeto.setTitulo(dados.getTitulo());
                    projeto.setAreaConhecimento(dados.getAreaConhecimento());
                    return ResponseEntity.ok(repository.save(projeto));
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id}/avaliar")
    public ResponseEntity<Projeto> avaliar(@PathVariable Long id, @RequestBody ProjetoDTO avaliacao) {
        if (avaliacao.getNotaInovacao() == null || avaliacao.getNotaApresentacao() == null) {
            return ResponseEntity.badRequest().build();
        }

        return repository.findById(id)
                .map(projeto -> {
                    BigDecimal mediaFinal = media(avaliacao.getNotaInovacao(), avaliacao.getNotaApresentacao());

                    projeto.setNotaInovacao(avaliacao.getNotaInovacao());
                    projeto.setNotaApresentacao(avaliacao.getNotaApresentacao());
                    projeto.setMediaFinal(mediaFinal);
                    projeto.setStatus(aprovar(mediaFinal));

                    return ResponseEntity.ok(repository.save(projeto));
                })
                .orElse(ResponseEntity.notFound().build());
    }


    public BigDecimal media(BigDecimal notaInovacao, BigDecimal notaApresentacao) {
        return notaInovacao.add(notaApresentacao)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }

    public TipoStatusEnum aprovar(BigDecimal mediaFinal) {
        return mediaFinal.compareTo(NOTA_MINIMA) >= 0
                ? TipoStatusEnum.APROVADO
                : TipoStatusEnum.REVISAR;
    }
}
