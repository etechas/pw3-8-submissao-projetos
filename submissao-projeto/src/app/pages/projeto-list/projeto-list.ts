import { Component, OnInit, inject, signal } from '@angular/core';
import { Projeto } from '../../models/projeto.model';
import { ProjetoService } from '../../services/projeto.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-projeto-list',
  imports: [RouterLink],
  templateUrl: './projeto-list.html',
  styleUrl: './projeto-list.css'
})
export class ProjetoList implements OnInit {
  private service = inject(ProjetoService);

  projetos = signal<Projeto[]>([]);
  erro = signal('');

  ngOnInit(): void {
    this.carregar();
  }

  // GET /api/projetos
  carregar(): void {
    this.service.listar().subscribe({
      next: dados => {
        this.projetos.set(dados);
        this.erro.set('');
      },
      error: () => this.erro.set('Não foi possível carregar os projetos. O backend está rodando?')
    });
  }

  // PATCH /api/projetos/{id}/avaliar (ação direta)
  avaliar(projeto: Projeto, notaInovacao: string, notaApresentacao: string): void {
    if (notaInovacao === '' || notaApresentacao === '') {
      alert('Informe as duas notas antes de avaliar.');
      return;
    }

    this.service
      .avaliar(projeto.id!, {
        notaInovacao: Number(notaInovacao),
        notaApresentacao: Number(notaApresentacao)
      })
      .subscribe({
        next: atualizado =>
          this.projetos.update(lista =>
            lista.map(p => (p.id === atualizado.id ? atualizado : p))
          ),
        error: () => alert('Erro ao avaliar o projeto.')
      });
  }

  // DELETE /api/projetos/{id}
  excluir(projeto: Projeto): void {
    if (!confirm(`Excluir o projeto "${projeto.titulo}"?`)) {
      return;
    }

    this.service.excluir(projeto.id!).subscribe({
      next: () =>
        this.projetos.update(lista => lista.filter(p => p.id !== projeto.id)),
      error: () => alert('Erro ao excluir o projeto.')
    });
  }
}