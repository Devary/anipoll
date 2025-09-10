import {Sharacter} from "./Sharacter";
import {Anime} from "./Anime";
import {Poll} from "./Poll";
import {VotingGroupType} from "./VotingGroupType";

export interface VotingGroup {
  id: string;
  sharacters :Sharacter[];
  animes :Anime[];
  polls :Poll[];
  groupResult : VotingGroup[];
  type:VotingGroupType;
}
