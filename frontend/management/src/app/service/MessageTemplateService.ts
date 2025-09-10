import {Injectable} from "@angular/core";
import {MessageService} from "primeng/api";
import {messageLife} from "../app.config";
import {MessageAction} from "./annotations/message-action";
import {EntityRegistry} from "./annotations/entity-registry";



@Injectable()
export class MessageTemplateService {

  constructor() {
  }

  public generateError(tuple:[EntityRegistry,MessageAction], technicalMessage:string): {  } {
    return {
      severity: 'error',
      summary: 'Error',
      detail: tuple[0] +" could not be "+ tuple[1]+" ("+technicalMessage+")",
      life: 3000
    }
  }
  public generateSuccess(tuple:[EntityRegistry,MessageAction]): {  } {
    return {
      severity: 'success',
      summary: 'Successful',
      detail: tuple[0] +" is successfully "+ tuple[1],
      life: messageLife
    }
  }
}
