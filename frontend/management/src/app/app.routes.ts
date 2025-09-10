import {Routes} from '@angular/router';
import {AppLayout} from "./layout/component/app.layout";
import {DynamicTableComponent} from "./modules/dynamic/dynamic-table/dynamic-table.component";

export const routes: Routes = [
  {
    path: '',
    component: AppLayout,
    children: [
      { path: 'modules', loadChildren: () => import('./modules/modules.routes') }
    ]
  },
  { path: 'dyn', component: DynamicTableComponent },
];
