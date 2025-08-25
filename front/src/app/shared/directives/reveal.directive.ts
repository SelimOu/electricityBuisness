import { Directive, ElementRef, Input, OnDestroy, OnInit } from '@angular/core';

@Directive({ selector: '[ebReveal]' })
export class RevealDirective implements OnInit, OnDestroy {
  @Input() revealAnimation: 'fade-up' | 'fade-in' | 'slide-left' | 'slide-right' = 'fade-up';
  @Input() revealDelay = 0;
  @Input() threshold = 0.2;
  private observer?: IntersectionObserver;

  constructor(private el: ElementRef<HTMLElement>) { }
  ngOnInit() {
    const native = this.el.nativeElement;
    native.style.opacity = '0';
    native.style.transition = 'all .9s cubic-bezier(.25,.8,.25,1)';
    switch (this.revealAnimation) {
      case 'slide-left': native.style.transform = 'translateX(40px)'; break;
      case 'slide-right': native.style.transform = 'translateX(-40px)'; break;
      case 'fade-up': native.style.transform = 'translateY(40px)'; break;
      default: native.style.transform = 'translateY(12px)';
    }
    this.observer = new IntersectionObserver(entries => {
      entries.forEach(e => {
        if (e.isIntersecting) {
          setTimeout(() => {
            native.style.opacity = '1';
            native.style.transform = 'translateX(0) translateY(0)';
          }, this.revealDelay);
          this.observer?.disconnect();
        }
      });
    }, { threshold: this.threshold });
    this.observer.observe(native);
  }
  ngOnDestroy() { this.observer?.disconnect(); }
}