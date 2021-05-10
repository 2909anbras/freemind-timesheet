import { Injectable } from '@angular/core';
// import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
// import { LoginModalComponent } from 'app/shared/login/login.component';

@Injectable({ providedIn: 'root' })
export class LoginModalService {
  private isOpen = false;

  constructor(
    // private modalService: NgbModal,
    private router: Router
  ) {}

  open(): void {
    this.router.navigate(['login']);
    // if (this.isOpen) {
    //   return;
    // }
    // this.isOpen = true;
    // const modalRef: NgbModalRef = this.modalService.open(LoginModalComponent);
    // modalRef.result.finally(() => (this.isOpen = false));
  }
}
