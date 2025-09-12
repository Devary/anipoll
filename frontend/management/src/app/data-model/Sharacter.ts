import {SharacterRole} from "./SaharacterRole";
import {Anime, AnimeImpl} from "./Anime";

export interface Sharacter {
  id?:string;
  name:string;
  description:string;
  roles:SharacterRole[];
  anime?:Anime;
  _deleted:boolean;
}

export class SharacterImpl implements Sharacter{
  name = "";
  description = "";
  roles= [];
  _deleted= false;
}
