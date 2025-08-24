import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'filter' })
export class FilterPipe implements PipeTransform {
    transform(items: any[], search: string): any[] {
        if (!items) return [];
        if (!search) return items;
        const s = search.toLowerCase();
        return items.filter(i => JSON.stringify(i).toLowerCase().includes(s));
    }
}
