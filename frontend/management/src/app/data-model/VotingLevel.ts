import {VotingGroup} from "./VotingGroup";

export interface VotingLevel {
  id:string;
  groups:VotingGroup[];
  version:bigint;
}
