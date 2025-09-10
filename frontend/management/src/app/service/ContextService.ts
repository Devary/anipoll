import {TableContext} from "../context/TableContext";
import {RoleService} from "./RoleService";
import {Injectable} from "@angular/core";
import {SharacterService} from "./SharacterService";
import {AnimeService} from "./AnimeService";
import {TableField} from "../context/models/TableField";
import {TableAction} from "../context/models/TableAction";


@Injectable()
export class ContextService {
  private _roleTableContext:TableContext;
  private _sharacterTableContext:TableContext;
  private _animeTableContext:TableContext;

  constructor(private roleService:RoleService,private sharacterService:SharacterService,private animeService:AnimeService) {
    this._roleTableContext = this.generateContextFor(RoleService.name);
    this._sharacterTableContext = this.generateContextFor(SharacterService.name);
    this._animeTableContext = this.generateContextFor(AnimeService.name);
  }

  generateContextFor(service:string) : TableContext{
      let context : TableContext = new TableContext();
      if (service == "_RoleService") {
        context.service = this.roleService;
      }else if (service == "_SharacterService") {
        context.service = this.sharacterService;
      }else if (service == "_AnimeService") {
        context.service = this.animeService;
      }else{
        throw new SyntaxError("Cannot determine service name");
      }
      return context;
  }

  public getTableContextFor(name:string): TableContext {
    switch (name){
      case "role": return this.roleTableContext;
      case "sharacter": return this.sharacterTableContext;
      case "anime": return this.animeTableContext;
      default: throw new SyntaxError("Cannot determine entity name");
    }
  }


  get roleTableContext(): TableContext {
    return this._roleTableContext;
  }
  get sharacterTableContext(): TableContext {
    return this._sharacterTableContext;
  }

  get animeTableContext(): TableContext {
    return this._animeTableContext;
  }

  createContextScheme(name:string,_fields : TableField[],allowActions:boolean,allowedActions:TableAction[],allowExportAction:boolean,disabledActions:string[],disableFields:string[]){
    this.validateSchema();
    return {
      name: name,
      fields: _fields,
      allowActions: allowActions,
      allowedActions: allowedActions,
      allowExportAction: allowExportAction,
      disabledActions: disabledActions,
      disableFields: disableFields
    };
  }
  validateSchema():void{
    //todo: make all possible validations
  }
}
