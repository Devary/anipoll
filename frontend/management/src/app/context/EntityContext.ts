import {TableAction} from "./models/TableAction";
import {TableField} from "./models/TableField";


export class EntityContext {
  constructor(init: {name: string, fields: TableField[], allowActions: boolean, allowedActions: TableAction[], allowExportAction: boolean,disabledActions: string[],disableFields: string[]}) {
      this.name = init.name;
      this.fields = init.fields;
      this.allowActions = init.allowActions;
      this.allowedActions = init.allowedActions;
      this.allowExportAction = init.allowExportAction;
      this.disabledActions = init.disabledActions;
      this.disabledFields = init.disableFields;
  }

  name: string="";
  fields : TableField[] =[];
  disabledFields : string[] =[];
  allowActions: boolean = true ;
  allowedActions: TableAction[] =[];
  disabledActions: string[] =[];
  allowExportAction: boolean =true;
}
