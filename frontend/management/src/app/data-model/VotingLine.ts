import {Sharacter} from "./Sharacter";
import {Poll} from "./Poll";

export interface VotingLine {
  id : string;
  name : string;
  description :string;
  sharacter:Sharacter[];
  Poll:Poll;
}
