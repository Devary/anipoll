import {AfterViewInit, Component, DestroyRef, OnDestroy, OnInit, signal, ViewChild} from '@angular/core';
import {EntityContext} from "../../context/EntityContext";
import {ContextService} from "../../service/ContextService";
import {Button} from "primeng/button";
import {ConfirmDialog} from "primeng/confirmdialog";
import {Dialog} from "primeng/dialog";
import {Editor} from "primeng/editor";
import {InputText} from "primeng/inputtext";
import {MultiSelect} from "primeng/multiselect";
import {NgForOf, NgIf, NgStyle, TitleCasePipe} from "@angular/common";
import {ConfirmationService, MessageService, PrimeTemplate} from "primeng/api";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Table, TableModule} from "primeng/table";
import {Tag} from "primeng/tag";
import {Toast} from "primeng/toast";
import {Toolbar} from "primeng/toolbar";
import {EntityRegistry} from "../../service/annotations/entity-registry";
import {MessageAction} from "../../service/annotations/message-action";
import {DialogService, DynamicDialogRef} from "primeng/dynamicdialog";
import {BehaviorSubject, catchError, EMPTY} from "rxjs";
import {MessageTemplateService} from "../../service/MessageTemplateService";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {ObjectListComponent} from "../dynamic/object-list/object-list.component";
import {tablePageSize} from "../../app.config";
import {AnimeService} from "../../service/AnimeService";
import {SharacterService} from "../../service/SharacterService";
import {Sharacter} from "../../data-model/Sharacter";

@Component({
  selector: 'app-anime',
  imports: [
    Button,
    ConfirmDialog,
    Dialog,
    Editor,
    InputText,
    MultiSelect,
    NgIf,
    PrimeTemplate,
    ReactiveFormsModule,
    TableModule,
    Tag,
    TitleCasePipe,
    Toast,
    Toolbar,
    FormsModule,
    NgStyle,
  ],
  templateUrl: './anime.component.html',
  standalone: true,
  styleUrl: './anime.component.scss'
})
export class AnimeComponent implements OnDestroy,OnInit,AfterViewInit{

  @ViewChild('dt') dt!: Table;
  protected creationDialog: boolean = false;
  protected isEdit = false;
  protected isCreate = false;
  object!: {};
  selectedObjects!: any[];
  objects = signal<any[]>([]);
  loading: boolean = true;
  tuple!: [EntityRegistry, MessageAction];
  showMoreDialog: DynamicDialogRef | undefined;
  context : any;
  protected readonly tablePageSize = tablePageSize;
  destroyed = new BehaviorSubject(null)
  availableSharacters = signal<Sharacter[]>([]);

  constructor(private destroyRef: DestroyRef,
              private confirmationService: ConfirmationService,
              private messageService: MessageService,
              private service: AnimeService,
              private messageTemplate: MessageTemplateService,
              private contextService: ContextService,
              private sharacterService: SharacterService,
              public dialogService: DialogService) {
    this.loading = true;
    this.object = {};

    this.context = new EntityContext(this.contextService.createContextScheme(
      "anime",
      [],
      true,
      [],
      true,
      [],
      ['id']
    ))
  }
  ngOnInit(): void {

  }

  ngAfterViewInit(): void {
    this.tuple = [EntityRegistry.getByName(this.context.name), EMPTY];
    console.log(this.context.name)
    this.service.getAll().subscribe((data: any[]) => this.objects.set(data));
    this.loading = false;
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
    this.updateAvailableSharactersList("new",{})
  }

  hideDialog() {
    this.creationDialog = false;
    this.isEdit = false;
    this.isCreate = false;
    this.object = {};
  }

  edit(obj: any) {
    this.object = obj;
    this.creationDialog = true;
    this.isEdit = true;
    console.log("Obj",obj)
    this.updateAvailableSharactersList("edit",obj);
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
      let _objects = this.objects();
      this.service.create(this.object)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .pipe(catchError((err) => {
          this.messageService.add(this.messageTemplate.generateError(this.tuple, err.statusText))
          return EMPTY;
        })).subscribe(res => {
        this.objects.set([..._objects, res]);
        this.messageService.add(this.messageTemplate.generateSuccess(this.tuple))
      });
      this.isCreate = false;
    }
    this.hideDialog();
  }

  deleteSelectedObjects() {
    this.confirmationService.confirm({
      message: this.selectedObjects.length > 1 ? 'Are you sure you want to delete the selected (' + this.selectedObjects.length + ') ' + this.context.name + '(s) ?' :
        'Are you sure you want to delete the selected (' + this.selectedObjects.length + ') ' + this.context.name + ' ?',
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
      header: 'List of ' + this.context.name + 's',
      //width: '70%',
      contentStyle: {overflow: 'auto'},
      baseZIndex: 10000,
      //maximizable: true,
      data: objElement,
      closable: true,
      autoZIndex:true,
      focusOnShow:true,
      modal:true,
      closeOnEscape:true
    });
  }

  private updateAvailableSharactersList(context: string, obj: any) {
    switch (context){
      case "edit" : this.appendCurrentSharacters(obj);
      case "new" : this.reloadAvailableSharacters();
      default : null;
    }
  }

  private appendCurrentSharacters(obj: any) {
    this.sharacterService.getNotLinkedToAnime().subscribe(data => {
      this.availableSharacters.set(data);
      obj.sharacters.forEach(sharacter => {
        this.availableSharacters.update( _sharacters => [..._sharacters,sharacter])
      });
    })
  }

  private reloadAvailableSharacters() {
    this.sharacterService.getNotLinkedToAnime().subscribe(data => {
      this.availableSharacters.set(data);
    })
  }
}
