import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { FreemindTimesheetTestModule } from '../../../test.module';
import { ToolUserUpdateComponent } from 'app/entities/tool-user/tool-user-update.component';
import { ToolUserService } from 'app/entities/tool-user/tool-user.service';
import { ToolUser } from 'app/shared/model/tool-user.model';

describe('Component Tests', () => {
  describe('ToolUser Management Update Component', () => {
    let comp: ToolUserUpdateComponent;
    let fixture: ComponentFixture<ToolUserUpdateComponent>;
    let service: ToolUserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [FreemindTimesheetTestModule],
        declarations: [ToolUserUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(ToolUserUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ToolUserUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ToolUserService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new ToolUser(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new ToolUser();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
