import {inject} from '@angular/core';
import {CanActivateFn, Router} from '@angular/router';
import {AuthService} from './auth.service';

//sans utilisateur connecté, redirection vers `/login?retour=<url demandée>`
 
export const authGuard: CanActivateFn= (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.utilisateur()){
    return true;
  }
  return router.createUrlTree(['/login'], { queryParams: {retour: state.url } });
};