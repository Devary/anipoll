import {
  AfterViewInit,
  Component,
  DestroyRef,
  input,
  OnDestroy,
  OnInit,
  signal,
  ViewChild
} from '@angular/core';
import {Table, TableModule} from "primeng/table";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Button} from "primeng/button";
import {MultiSelectModule} from "primeng/multiselect";
import {DropdownModule} from "primeng/dropdown";
import {InputTextModule} from "primeng/inputtext";
import {Toolbar} from "primeng/toolbar";
import {Dialog} from "primeng/dialog";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faCoffee} from "@fortawesome/free-solid-svg-icons";
import {BehaviorSubject, catchError, EMPTY} from "rxjs";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {NgForOf, NgIf, TitleCasePipe} from "@angular/common";
import {ConfirmationService, MessageService} from "primeng/api";
import {ConfirmDialog} from "primeng/confirmdialog";
import {Editor} from "primeng/editor";
import {Toast} from "primeng/toast";
import {tablePageSize} from "../../../app.config";
import {MessageTemplateService} from "../../../service/MessageTemplateService";
import {EntityRegistry} from "../../../service/annotations/entity-registry";
import {MessageAction} from "../../../service/annotations/message-action";
import {ContextService} from "../../../service/ContextService";
import {EntityContext} from "../../../context/EntityContext";
import {TableField} from "../../../context/models/TableField";
import {Tag} from "primeng/tag";
import {DialogService, DynamicDialogRef} from "primeng/dynamicdialog";
import {ObjectListComponent} from "../object-list/object-list.component";
import {Select} from "primeng/select";
import {CustomCallService} from "../../../service/CustomCallService";


@Component({
  selector: 'd-dt',
  standalone: true,
  imports: [
    TableModule,
    FormsModule,
    Button,
    MultiSelectModule,
    DropdownModule,
    InputTextModule,
    Toolbar,
    Dialog,
    FaIconComponent,
    ReactiveFormsModule,
    ConfirmDialog,
    Editor,
    Toast,
    NgIf,
    NgForOf,
    Tag,
    TitleCasePipe,
    Select,
  ],
  templateUrl: './dynamic-table.component.html',
  styleUrl: './dynamic-table.component.scss',
  outputs: ['message', 'visibleConfirmation'],
  providers: [ConfirmationService, MessageService, ContextService],
})
export class DynamicTableComponent implements OnInit, AfterViewInit, OnDestroy {

  //views
  @ViewChild('dt') dt!: Table;

  //variables
  protected creationDialog: boolean = false;
  protected isEdit = false;
  protected isCreate = false;
  object!: {};
  selectedObjects!: any[];
  objects = signal<any[]>([]);
  loading: boolean = true;
  tuple!: [EntityRegistry, MessageAction];
  defaultInitContext!: any;

  showMoreDialog: DynamicDialogRef | undefined;


  context = input<EntityContext>(this.defaultInitContext);
  protected readonly tablePageSize = tablePageSize;
  service: any;
  //icons
  protected readonly faCoffee = faCoffee;

  //behavior
  destroyed = new BehaviorSubject(null)
  fieldsToShow: TableField[] = [];
  fieldsToShowStrings: string[] = [];

  constructor(private destroyRef: DestroyRef,
              private confirmationService: ConfirmationService,
              private messageService: MessageService,
              private messageTemplate: MessageTemplateService,
              public dialogService: DialogService,
              public customCallService: CustomCallService,
              private contextService: ContextService) {
    this.loading = true;
    this.object = {};
  }


  ngOnInit(): void {

  }

  generateFieldsToShow() {
    this.context().fields.forEach(field => {
      if (this.context().disabledFields?.filter(fth => fth === field.name).length === 0) {
        this.fieldsToShow.push(field);
      }
    });
  }

  ngAfterViewInit(): void {
    //init service
    this.service = this.contextService.getTableContextFor(this.context()?.name).service;
    this.tuple = [EntityRegistry.getByName(this.context()?.name), EMPTY];
    console.log(this.context()?.name)
    this.service.getAll().subscribe((data: any[]) => this.objects.set(data));
    this.generateFieldsToShow();
    this.fieldsToShow.map(field => {
        if (field.isMultiSelect || field.isSelect) {
          if (field.listObjects?.length === 0 && field.fieldObjectsFilter!==undefined) {
            // @ts-ignore
            this.customCallService.call(field.customCallParams.customCallLink).subscribe(data => field.setListObjects(this.filterData(field,data)))
          } else {
            // @ts-ignore
            this.contextService.getTableContextFor(field.listType).service.getAll().pipe().subscribe(data => field.setListObjects(this.filterData(field,data))
            )
          }
        }
      }
    )
    this.loading = false;
  }
  filterData(field:TableField,data:any):any{
    this.object[field.name]= field.listObjects;
    if (field.fieldObjectsFilter === undefined){
      return data;
    }
    let _data:any[] = [];
    //if (field.customCallParams !== undefined) {
    //  //todo:lezem t3adi l'id mta3 l'anime
    //  this.customCallService.callWithParams(field.customCallParams.customCallLink,field.customCallParams.customCallParams.get("attr")).subscribe()
    //}
    data.forEach(obj => {
      // @ts-ignore
      console.log(obj[field.fieldObjectsFilter?.filterName]===field.fieldObjectsFilter?.FilterValue)
      // @ts-ignore
      if ( obj[field.fieldObjectsFilter?.filterName] === field.fieldObjectsFilter?.FilterValue){
        _data.push(obj);
      }
       //field.listObjects?.push(obj);
    })
    console.log(_data)
    console.log(field.listObjects)
    console.log(this.object[field.name])
    console.log(this.objects().at(this.object["index"]))
    //_data.push(field.listObjects);
    //console.log(_data,field.listObjects,this.object[field.name]);
    //this.object[field.name]= field.listObjects;
    return _data;
  }

  ngOnDestroy(): void {
    this.destroyed.next(null);
    this.destroyed.complete();
  }

  delete(obj: Object) {
    this.selectedObjects = [];
    this.selectedObjects.push(obj);
    this.deleteSelectedObjects();
  }

  openNew() {
    this.object = {};
    this.creationDialog = true;
    this.isCreate = true;
  }

  hideDialog() {
    this.creationDialog = false;
    this.isEdit = false;
    this.isCreate = false;
    this.object = {};
  }

  exportCSV() {
    this.dt.exportCSV();
  }

  edit(obj: any, rowIndex: number) {
    this.object = obj;
    this.creationDialog = true;
    this.isEdit = true;
    this.object["index"] = rowIndex;
  }

  processEntity() {
    if (this.isEdit) {
      this.changeActionTransition(MessageAction.SAVED);
      this.service.update(this.object).pipe(catchError((err) => {
        this.messageService.add(this.messageTemplate.generateError(this.tuple, err.statusText))
        return EMPTY;
      }))
        .subscribe(res => this.messageService.add(this.messageTemplate.generateSuccess(this.tuple)));
      this.isEdit = false;
    } else if (this.isCreate) {
      this.changeActionTransition(MessageAction.CREATED);
      let _roles = this.objects();
      this.service.create(this.object)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .pipe(catchError((err) => {
          this.messageService.add(this.messageTemplate.generateError(this.tuple, err.statusText))
          return EMPTY;
        })).subscribe(res => {
        this.objects.set([..._roles, res]);
        this.messageService.add(this.messageTemplate.generateSuccess(this.tuple))
      });
      this.isCreate = false;
    }
    this.hideDialog();
  }

  deleteSelectedObjects() {
    this.confirmationService.confirm({
      message: this.selectedObjects.length > 1 ? 'Are you sure you want to delete the selected (' + this.selectedObjects.length + ') ' + this.context()?.name + '(s) ?' :
        'Are you sure you want to delete the selected (' + this.selectedObjects.length + ') ' + this.context()?.name + ' ?',
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: "Yes delete",
      acceptIcon: "pi pi-trash",
      acceptButtonStyleClass: "danger",
      accept: () => {
        this.changeActionTransition(MessageAction.DELETED);
        this.selectedObjects.forEach(sc => {
          this.service
            .delete(sc.id)
            .pipe(catchError((err) => {
              this.messageService.add(this.messageTemplate.generateError(this.tuple, err.statusText))
              return EMPTY;
            }))
            .subscribe(res => this.messageService.add(this.messageTemplate.generateSuccess(this.tuple)))
        })
        this.objects.set(this.objects().filter((val) => !this.selectedObjects?.includes(val)));
        this.selectedObjects = [];
      },
      reject: () => {
        this.hideDialog()
      }
    });
  }

  changeActionTransition(action: MessageAction) {
    this.tuple[1] = action;
  }

  showMore(objElement: any) {
    console.log(objElement)
    this.showMoreDialog = this.dialogService.open(ObjectListComponent, {
      header: 'List of ' + this.context().name + 's',
      //width: '70%',
      contentStyle: {overflow: 'auto'},
      baseZIndex: 10000,
      //maximizable: true,
      data: objElement,
      closable: true,

    });
  }

  debug() {
    this.fieldsToShow.forEach(obj => {
      if (obj.isMultiSelect){
        console.log(obj.listObjects)
        console.log(this.object[obj.name])
      }
    })
  }
}

