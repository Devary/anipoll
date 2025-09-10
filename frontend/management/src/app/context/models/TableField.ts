import {TableFieldType} from "./TableFieldType";
import {ObjectFilter} from "./ObjectFilter";
import {CustomCallParams} from "./CustomCallParams";

export class TableField {
  name: string;
  type: TableFieldType;
  listType?: string; ///object Type in case of the type is multiselect
  listObjects?: any[]; ///object Type in case of the type is multiselect
  customCallParams? : CustomCallParams;
  fieldObjectsFilter?: ObjectFilter;

  constructor(name: string, type: TableFieldType, listType?: string,listObjects?: any[],customCallParams?:CustomCallParams,fieldObjectsFilter?: ObjectFilter) {
    this.name = name;
    this.type = type;
    this.listType = listType;
    this.listObjects = listObjects;
    this.customCallParams = customCallParams;
    this.fieldObjectsFilter = fieldObjectsFilter;
  }

  get isMultiSelect(){
    return this.type === TableFieldType.MULTI_SELECT;
  }
  get isTextEditor(){
    return this.type === TableFieldType.TEXT_EDITOR;
  }
  get isInputText(){
    return this.type === TableFieldType.INPUT_TEXT;
  }
  public setListObjects(listObjects:any[]){
    this.listObjects = listObjects;
  }

  get isSelect(){
    return this.type === TableFieldType.SELECT;
  }
}
