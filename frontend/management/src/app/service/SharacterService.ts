import {Injectable} from "@angular/core";
import {WebClientService} from "./web-client.service"
import {Observable} from "rxjs";
import {Sharacter} from "../data-model/Sharacter";
import {AnimeService} from "./AnimeService";


@Injectable()
export class SharacterService{

  INITIAL: string = "/sharacters";
  URI : string;
  UPLOAD_URL: string = this.service.buildURI()+"/v1/upload";

  constructor(private service : WebClientService,private animeService : AnimeService) {
    this.URI = service.buildURI()+this.INITIAL;
  }

  getOne(id: string) {
    return this.service.getClient().get<Sharacter>(this.URI+"/"+id);
  }


  getAll() {
    return this.service.getClient().get<Sharacter[]>(this.URI);
  }

  getNotLinkedToAnime() {
    return this.service.getClient().get<Sharacter[]>(this.URI+"/wa/");
  }

  create(sharacter: Sharacter) {
    return this.service.getClient().post<Sharacter>(this.URI,sharacter);
  }

  delete(id: string) {
    return this.service.getClient().delete<string>(this.URI+"/"+id);
  }
  update(data: Sharacter) {
    console.log("update", data);
    return this.service.getClient().put<Sharacter>(this.URI+"/"+data.id,data);
  }

  uploadImage(formData: FormData) : Observable<any> {
    return this.service.getClient().post<any>(this.service.buildURI()+"/upload",formData);
  }
}
