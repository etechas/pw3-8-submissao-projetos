import { Routes } from '@angular/router';
import { ProjetoList } from './pages/projeto-list/projeto-list';

export const routes: Routes = [
    { path: '', redirectTo: 'projetos', pathMatch: 'full' },
    { path: 'projetos', component: ProjetoList }
];
