import {Injectable} from "@angular/core";
import {WebClientService} from "./web-client.service"
import {SharacterRole} from "../data-model/SaharacterRole";
import {Poll} from "../data-model/Poll";


@Injectable()
export class PollService{

  INITIAL: string = "/polls";
  URI : string;

  constructor(private service : WebClientService) {
    this.URI = service.buildURI()+this.INITIAL;
  }

  getOne(id: string) {
    return this.service.getClient().get<SharacterRole>(this.URI+"/"+id);
  }
  getAll() {
    return this.service.getClient().get<SharacterRole[]>(this.URI);
  }
  create(poll: any) {
    return this.service.getClient().post<Poll>(this.URI,poll);
  }

  delete(id: string) {
    return this.service.getClient().delete<string>(this.URI+"/"+id);
  }
  update(poll: any) {
    return this.service.getClient().put<SharacterRole>(this.URI+"/"+poll.id,poll);
  }

}
