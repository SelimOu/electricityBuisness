import { Component, Input, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'eb-hero',
  templateUrl: './hero.component.html',
  styleUrls: ['./hero.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HeroComponent {
  @Input() title = '';
  @Input() subtitle = '';
  @Input() background = '';// image or video url
  @Input() height: 'sm' | 'md' | 'lg' = 'md';
  @Input() ctaLabel?: string;
  @Input() ctaRouterLink?: string;
  @Input() overlayStrength: 'light' | 'normal' | 'strong' = 'normal';
  get heroClasses() {
    return {
      'hero-base': true,
      'min-h-[40vh]': this.height === 'sm',
      'min-h-[60vh]': this.height === 'md',
      'min-h-[80vh]': this.height === 'lg'
    };
  }
  get overlayClass() {
    switch (this.overlayStrength) {
      case 'light': return 'hero-overlay opacity-60';
      case 'strong': return 'hero-overlay opacity-95';
      default: return 'hero-overlay opacity-80';
    }
  }
}