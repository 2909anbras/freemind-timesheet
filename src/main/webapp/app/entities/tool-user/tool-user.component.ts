import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IToolUser } from 'app/shared/model/tool-user.model';
import { ToolUserService } from './tool-user.service';
import { ToolUserDeleteDialogComponent } from './tool-user-delete-dialog.component';

@Component({
  selector: 'jhi-tool-user',
  templateUrl: './tool-user.component.html',
})
export class ToolUserComponent implements OnInit, OnDestroy {
  toolUsers?: IToolUser[];
  eventSubscriber?: Subscription;

  constructor(protected toolUserService: ToolUserService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.toolUserService.query().subscribe((res: HttpResponse<IToolUser[]>) => (this.toolUsers = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInToolUsers();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IToolUser): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInToolUsers(): void {
    this.eventSubscriber = this.eventManager.subscribe('toolUserListModification', () => this.loadAll());
  }

  delete(toolUser: IToolUser): void {
    const modalRef = this.modalService.open(ToolUserDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.toolUser = toolUser;
  }
}
