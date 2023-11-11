import { Component, OnInit, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit, OnDestroy {
  // Your component properties and methods here

  ngOnInit() {
    // Component initialization
  }

  ngOnDestroy() {
    // Cleanup logic
  }
}
