package joaopedro_guilherme.submissao_projetos.controller;

import joaopedro_guilherme.submissao_projetos.dto.ProjetoDTO;
import joaopedro_guilherme.submissao_projetos.exception.RecursoNaoEncontradoException;
import joaopedro_guilherme.submissao_projetos.exception.RegraNegocioException;
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

    private static final BigDecimal NOTA_MINIMA_APROVACAO = BigDecimal.valueOf(7);
    private static final BigDecimal NOTA_MAXIMA = BigDecimal.TEN;

    private final ProjetoRepository repository;

    public ProjetoController(ProjetoRepository repository) {
        this.repository = repository;
    }


    @GetMapping
    public List<Projeto> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Projeto buscar(@PathVariable Long id) {
        return buscarOuFalhar(id);
    }


    @PostMapping
    public ResponseEntity<Projeto> criar(@RequestBody Projeto dados) {
        validarDadosBasicos(dados);

        Projeto projeto = new Projeto();
        projeto.setTitulo(dados.getTitulo().trim());
        projeto.setAreaConhecimento(dados.getAreaConhecimento().trim());

        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(projeto));
    }


    @PutMapping("/{id}")
    public Projeto atualizar(@PathVariable Long id, @RequestBody Projeto dados) {
        validarDadosBasicos(dados);

        Projeto projeto = buscarOuFalhar(id);
        projeto.setTitulo(dados.getTitulo().trim());
        projeto.setAreaConhecimento(dados.getAreaConhecimento().trim());
        return repository.save(projeto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        buscarOuFalhar(id);
        repository.deleteById(id);
    }


    @PatchMapping("/{id}/avaliar")
    public Projeto avaliar(@PathVariable Long id, @RequestBody ProjetoDTO dto) {
        validarNota("de inovação", dto.getNotaInovacao());
        validarNota("de apresentação", dto.getNotaApresentacao());

        Projeto projeto = buscarOuFalhar(id);
        BigDecimal mediaFinal = media(dto.getNotaInovacao(), dto.getNotaApresentacao());

        projeto.setNotaInovacao(dto.getNotaInovacao());
        projeto.setNotaApresentacao(dto.getNotaApresentacao());
        projeto.setMediaFinal(mediaFinal);
        projeto.setStatus(aprovar(mediaFinal));

        return repository.save(projeto);
    }


    public BigDecimal media(BigDecimal notaInovacao, BigDecimal notaApresentacao) {
        return notaInovacao.add(notaApresentacao)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }

    public TipoStatusEnum aprovar(BigDecimal mediaFinal) {
        return mediaFinal.compareTo(NOTA_MINIMA_APROVACAO) >= 0
                ? TipoStatusEnum.APROVADO
                : TipoStatusEnum.REVISAR;
    }

    private Projeto buscarOuFalhar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto " + id + " não encontrado."));
    }

    private void validarDadosBasicos(Projeto dados) {
        if (dados.getTitulo() == null || dados.getTitulo().isBlank()) {
            throw new RegraNegocioException("O título do projeto é obrigatório.");
        }
        if (dados.getAreaConhecimento() == null || dados.getAreaConhecimento().isBlank()) {
            throw new RegraNegocioException("A área de conhecimento é obrigatória.");
        }
    }

    private void validarNota(String nome, BigDecimal nota) {
        if (nota == null) {
            throw new RegraNegocioException("A nota " + nome + " é obrigatória.");
        }
        if (nota.compareTo(BigDecimal.ZERO) < 0 || nota.compareTo(NOTA_MAXIMA) > 0) {
            throw new RegraNegocioException("A nota " + nome + " deve estar entre 0 e 10.");
        }
    }
}