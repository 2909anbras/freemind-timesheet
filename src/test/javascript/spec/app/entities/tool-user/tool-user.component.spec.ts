import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { FreemindTimesheetTestModule } from '../../../test.module';
import { ToolUserComponent } from 'app/entities/tool-user/tool-user.component';
import { ToolUserService } from 'app/entities/tool-user/tool-user.service';
import { ToolUser } from 'app/shared/model/tool-user.model';

describe('Component Tests', () => {
  describe('ToolUser Management Component', () => {
    let comp: ToolUserComponent;
    let fixture: ComponentFixture<ToolUserComponent>;
    let service: ToolUserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [FreemindTimesheetTestModule],
        declarations: [ToolUserComponent],
      })
        .overrideTemplate(ToolUserComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ToolUserComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ToolUserService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new ToolUser(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.toolUsers && comp.toolUsers[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
