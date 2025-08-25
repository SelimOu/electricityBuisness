import { Directive, ElementRef, HostListener, Input } from '@angular/core';

@Directive({
    selector: '[ebParallax]'
})
export class ParallaxDirective {
    @Input() parallaxRatio = 0.4; // smaller = slower movement
    private initialY = 0;

    constructor(private el: ElementRef<HTMLElement>) {
        this.initialY = this.el.nativeElement.offsetTop;
        this.apply();
    }

    @HostListener('window:scroll') onScroll() { this.apply(); }

    private apply() {
        const scrolled = window.scrollY;
        const translate = Math.round(scrolled * this.parallaxRatio);
        this.el.nativeElement.style.transform = `translate3d(0, ${translate}px, 0)`;
    }
}