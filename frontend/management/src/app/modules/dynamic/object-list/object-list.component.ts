import {Component} from '@angular/core';
import {DynamicDialogConfig} from "primeng/dynamicdialog";
import {Tag} from "primeng/tag";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-object-list',
  imports: [
    Tag,
    NgForOf
  ],
  templateUrl: './object-list.component.html',
  standalone: true,
  styleUrl: './object-list.component.scss'
})
export class ObjectListComponent {

  data :any;

  constructor(public config: DynamicDialogConfig) {
    this.data = config.data;
  }
}
