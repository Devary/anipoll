import { Routes } from '@angular/router';
import {SharacterComponent} from "./sharacter/sharacter.component";
import {RoleComponent} from "./role/role.component";
import {AnimeComponent} from "./anime/anime.component";
import {PollComponent} from "./poll/poll.component";

export default [
  {path: "sharacter", component: SharacterComponent},
  {path: "role", component: RoleComponent},
  {path: "anime", component: AnimeComponent},
  {path: "poll", component: PollComponent},
  //{ path: '**', redirectTo: '/notfound' }
] as Routes;
