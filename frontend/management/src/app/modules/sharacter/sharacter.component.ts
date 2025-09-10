import {AfterViewInit, Component, DestroyRef, OnDestroy, OnInit, signal, ViewChild} from '@angular/core';
import {ConfirmationService, MessageService, PrimeTemplate} from "primeng/api";
import {EntityContext} from "../../context/EntityContext";
import {ContextService} from "../../service/ContextService";
import {DynamicTableComponent} from "../dynamic/dynamic-table/dynamic-table.component";
import {Table, TableModule} from "primeng/table";
import {EntityRegistry} from "../../service/annotations/entity-registry";
import {MessageAction} from "../../service/annotations/message-action";
import {DialogService, DynamicDialogRef} from "primeng/dynamicdialog";
import {BehaviorSubject, catchError, EMPTY} from "rxjs";
import {Sharacter, SharacterImpl} from "../../data-model/Sharacter";
import {MessageTemplateService} from "../../service/MessageTemplateService";
import {SharacterService} from "../../service/SharacterService";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {tablePageSize} from "../../app.config";
import {Button} from "primeng/button";
import {ConfirmDialog} from "primeng/confirmdialog";
import {Dialog} from "primeng/dialog";
import {Editor} from "primeng/editor";
import {InputText} from "primeng/inputtext";
import {MultiSelect} from "primeng/multiselect";
import {NgIf, NgStyle, TitleCasePipe} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Tag} from "primeng/tag";
import {Toast} from "primeng/toast";
import {Toolbar} from "primeng/toolbar";
import {Anime} from "../../data-model/Anime";
import {AnimeService} from "../../service/AnimeService";
import {Select} from "primeng/select";


@Component({
  selector: 'app-sharacter',
  standalone: true,
  imports: [
    Button,
    ConfirmDialog,
    Dialog,
    Editor,
    InputText,
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
    Select

  ],
  templateUrl: './sharacter.component.html',
  styleUrl: './sharacter.component.scss',
  outputs: ['message','visibleConfirmation'],
  providers: [ConfirmationService, MessageService]
})
export class SharacterComponent implements OnInit,OnDestroy,AfterViewInit{

  @ViewChild('dt') dt!: Table;
  protected creationDialog: boolean = false;
  protected isEdit = false;
  protected isCreate = false;
  object!: Sharacter;
  selectedObjects!: any[];
  objects = signal<Sharacter[]>([]);
  loading: boolean = true;
  tuple!: [EntityRegistry, MessageAction];
  showMoreDialog: DynamicDialogRef | undefined;
  context : any;
  protected readonly tablePageSize = tablePageSize;
  destroyed = new BehaviorSubject(null)
  animeList = signal<Anime[]>([]);

  constructor(private destroyRef: DestroyRef,
              private confirmationService: ConfirmationService,
              private messageService: MessageService,
              private service: SharacterService,
              private animeService: AnimeService,
              private messageTemplate: MessageTemplateService,
              private contextService: ContextService,
              public dialogService: DialogService) {
    this.loading = true;
    this.animeService.getAll().subscribe(data => this.animeList.set(data))
    this.context = new EntityContext(this.contextService.createContextScheme(
      "sharacter",
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
    this.object = new SharacterImpl();
    this.creationDialog = true;
    this.isCreate = true;
  }

  hideDialog() {
    this.creationDialog = false;
    this.isEdit = false;
    this.isCreate = false;
    this.object = new SharacterImpl();
  }

  edit(obj: any) {
    this.object = obj;
    this.creationDialog = true;
    this.isEdit = true;
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

}
