import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FreemindTimesheetTestModule } from '../../../test.module';
import { ToolUserDetailComponent } from 'app/entities/tool-user/tool-user-detail.component';
import { ToolUser } from 'app/shared/model/tool-user.model';

describe('Component Tests', () => {
  describe('ToolUser Management Detail Component', () => {
    let comp: ToolUserDetailComponent;
    let fixture: ComponentFixture<ToolUserDetailComponent>;
    const route = ({ data: of({ toolUser: new ToolUser(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [FreemindTimesheetTestModule],
        declarations: [ToolUserDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(ToolUserDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ToolUserDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load toolUser on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.toolUser).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
