import {Type} from "@angular/core";

class FieldRow {
  name!:string;
  value!:any;
  options!:Map<string, any>;
}

export class TableContext {
   _entity_name!: string;
   _fields!: FieldRow[];
   _service!: any;


  get entityName(): string {
    return this._entity_name;
  }

  set entityName(value: string) {
    this._entity_name = value;
  }

  get fields(): FieldRow[] {
    return this._fields;
  }

  set fields(value: FieldRow[]) {
    this._fields = value;
  }

  get service(): any {
    return this._service;
  }

  set service(value: any) {
    this._service = value;
  }
}
