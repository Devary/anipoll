import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {MenuItem} from 'primeng/api';
import {AppMenuitem} from './app.menuitem';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, AppMenuitem, RouterModule],
  template: `
    <ul class="layout-menu">
      <ng-container *ngFor="let item of model; let i = index">
        <li app-menuitem *ngIf="!item.separator" [item]="item" [index]="i" [root]="true"></li>
        <li *ngIf="item.separator" class="menu-separator"></li>
      </ng-container>
    </ul> `
})
export class AppMenu {
  model: MenuItem[] = [];

  ngOnInit() {
    this.model = [
      {
        label: 'Home',
        items: [{label: 'Dashboard', icon: 'pi pi-fw pi-home', routerLink: ['/']}]
      },
      {
        label: 'CRUD',
        items: [
          {label: 'Characters', icon: 'pi pi-fw pi-android', routerLink: ['/modules/sharacter']},
          {label: 'Roles', icon: 'pi pi-fw pi-user', routerLink: ['/modules/role']},
          {label: 'Anime', icon: 'pi pi-fw pi-video', routerLink: ['/modules/anime']},
        ]
      },
      {
        label: 'Statistics',
        items: [
          {label: 'Polls', icon: 'pi pi-fw pi-wave-pulse', routerLink: ['/modules/stats/polls']},
          {label: 'Character', icon: 'pi pi-fw pi-wave-pulse', routerLink: ['/modules/stats/character']},
          {label: 'Anime', icon: 'pi pi-fw pi-wave-pulse', routerLink: ['/modules/stats/anime']},
        ]
      },
      {
        label: 'Polls Workflow',
        items: [
          {label: 'Single Poll', icon: 'pi pi-fw pi-plus', routerLink: ['/modules/single-poll']},
          {label: 'Multi Poll', icon: 'pi pi-fw pi-plus', routerLink: ['/modules/multi-poll']},
        ]
      }
    ];
  }
}
