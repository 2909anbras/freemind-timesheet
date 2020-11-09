import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IToolUser } from 'app/shared/model/tool-user.model';

@Component({
  selector: 'jhi-tool-user-detail',
  templateUrl: './tool-user-detail.component.html',
})
export class ToolUserDetailComponent implements OnInit {
  toolUser: IToolUser | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ toolUser }) => (this.toolUser = toolUser));
  }

  previousState(): void {
    window.history.back();
  }
}
