import { Component, AfterViewInit } from '@angular/core';
import * as maplibregl from 'maplibre-gl';
import { ApiService } from '../../services/api.service';
import { Router } from '@angular/router';

@Component({
    selector: 'carte',
    templateUrl: './carte.component.html',
    styleUrls: ['./carte.component.css']
})
export class CarteComponent implements AfterViewInit {
    map: any;
    constructor(private api: ApiService, private router: Router) { }
    ngAfterViewInit() {
        try {
            // Use a simple raster style based on OpenStreetMap tiles.
            // Raster tiles avoid vector sprite/glyph fetch issues and reliably show streets/labels.
            const osmStyle = {
                version: 8,
                sources: {
                    'raster-tiles': {
                        type: 'raster',
                        tiles: ['https://a.tile.openstreetmap.org/{z}/{x}/{y}.png'],
                        tileSize: 256,
                        attribution: '© OpenStreetMap contributors'
                    }
                },
                layers: [
                    { id: 'osm-tiles', type: 'raster', source: 'raster-tiles' }
                ]
            } as any;

            this.map = new (maplibregl as any).Map({ container: 'map', style: osmStyle, center: [2.3522, 48.8566], zoom: 13 });
            // add zoom/rotation controls
            (this.map as any).addControl(new (maplibregl as any).NavigationControl());
        } catch (e) {
            console.error('Map init error', e);
            return;
        }
        this.api.list('bornes').subscribe((b: any[]) => {
            b.forEach(borne => {
                // backend stores coordGPS as "lat,lon" string
                const coords = borne.coordGPS || borne.coord || '';
                const parts = String(coords).split(',').map(p => p.trim());
                if (parts.length === 2) {
                    const lat = parseFloat(parts[0]);
                    const lon = parseFloat(parts[1]);
                    if (!isNaN(lat) && !isNaN(lon)) {
                        const el = document.createElement('div');
                        el.className = 'marker-neon';

                        const popupNode = document.createElement('div');
                        popupNode.className = 'popup-glass p-3 rounded-lg text-sm';
                        popupNode.style.background = 'rgba(255,255,255,0.06)';
                        popupNode.style.backdropFilter = 'blur(8px)';
                        popupNode.style.color = '#000';
                        popupNode.innerHTML = `<div class="font-semibold">${borne.nomBorne || borne.nom}</div>
                            <div class="text-xs">Tarif: ${borne.tarif} €/min</div>
                            <div class="text-xs">Puissance: ${borne.puissance || '-'} kW</div>
                            <div class="mt-2"><button id="reserve-${borne.id}" class="px-3 py-1 rounded bg-cyan-600 text-black">Réserver</button></div>`;

                        const popup = new (maplibregl as any).Popup({ closeButton: true, closeOnClick: true }).setDOMContent(popupNode);
                        // Use MapLibre's built-in popup binding — let clicking the marker open its popup.
                        const marker = new (maplibregl as any).Marker(el).setLngLat([lon, lat]).setPopup(popup).addTo(this.map);
                        // Attach click handler directly on the popup node's button
                        // Use capture of borne.id in closure
                        setTimeout(() => {
                            const btn = popupNode.querySelector(`#reserve-${borne.id}`) as HTMLButtonElement | null;
                            if (btn) btn.addEventListener('click', (ev) => {
                                ev.stopPropagation();
                                this.router.navigate(['/reservation'], { queryParams: { borne: borne.id } });
                            });
                        }, 50);
                    }
                }
            });
        });
    }
}
