export class CustomCallParams {
  customCallLink?: string; ///object Type in case of the type is multiselect
  customCallParams?: Map<string,any>; ///object Type in case of the type is multiselect
  constructor(customCallLink?:string,customCallParams?: Map<string,any>) {
    this.customCallLink = customCallLink;
    this.customCallParams = customCallParams;

  }

}
