import {Injectable} from "@angular/core";
import { HttpClient } from "@angular/common/http";

@Injectable()
export class WebClientService {

  HOST: string = "http://localhost";
  PORT: string = ":9100";
  constructor(private http: HttpClient) {
    this.buildURI();
  }

  buildURI() {
    return this.HOST+this.PORT;
  }

  getClient(): HttpClient {
    return this.http;
  }

}
