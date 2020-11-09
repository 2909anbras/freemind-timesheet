import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IToolUser } from 'app/shared/model/tool-user.model';
import { ToolUserService } from './tool-user.service';

@Component({
  templateUrl: './tool-user-delete-dialog.component.html',
})
export class ToolUserDeleteDialogComponent {
  toolUser?: IToolUser;

  constructor(protected toolUserService: ToolUserService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.toolUserService.delete(id).subscribe(() => {
      this.eventManager.broadcast('toolUserListModification');
      this.activeModal.close();
    });
  }
}
