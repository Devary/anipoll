import {Sharacter} from "./Sharacter";
import {VotingLine} from "./VotingLine";

export interface Poll {
  id : string;
  name : string;
  description :string;
  sharacters:Sharacter[];
  voting_lines:VotingLine[];
}
