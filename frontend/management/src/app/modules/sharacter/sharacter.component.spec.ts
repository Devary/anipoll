import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SharacterComponent } from './sharacter.component';

describe('SharacterComponent', () => {
  let component: SharacterComponent;
  let fixture: ComponentFixture<SharacterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharacterComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SharacterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
