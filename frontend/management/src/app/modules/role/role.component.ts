import {Component} from '@angular/core';
import {ConfirmationService, MessageService} from "primeng/api";
import {DynamicTableComponent} from "../dynamic/dynamic-table/dynamic-table.component";
import {EntityContext} from "../../context/EntityContext";
import {ContextService} from "../../service/ContextService";
import {TableField} from "../../context/models/TableField";
import {TableFieldType} from "../../context/models/TableFieldType";

@Component({
  selector: 'app-role',
  standalone: true,
    imports: [DynamicTableComponent],
  templateUrl: './role.component.html',
  styleUrl: './role.component.scss',
  providers: [ConfirmationService, MessageService]
})
export class RoleComponent {

  context!:EntityContext;

  constructor(private contextService:ContextService) {
      let _fields:TableField[]=[];
      var name : TableField= new TableField("name",TableFieldType.INPUT_TEXT);
      var description : TableField= new TableField("description",TableFieldType.TEXT_EDITOR,);
      _fields.push(name,description)
      this.context= new EntityContext(this.contextService.createContextScheme(
        "role",
        _fields,
        true,
        [],
        true,
        [],
        ['id']));
    }
}
