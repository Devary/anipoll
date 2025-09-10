export class ObjectFilter {
  filterCriteria: string;
  filterName: string;
  FilterValue: any;

  constructor(filterCriteria: string, filterName: string, FilterValue: any) {
    this.filterCriteria = filterCriteria;
    this.filterName = filterName;
    this.FilterValue = FilterValue;
  }
}
