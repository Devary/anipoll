import {Sharacter} from "./Sharacter";

export interface Anime{
  id : string;
  name : string;
  description :string;
  sharacters:Sharacter[];
}

export class AnimeImpl implements Anime {
  description = "";
  id = "";
  name= "";
  sharacters = [];
}
