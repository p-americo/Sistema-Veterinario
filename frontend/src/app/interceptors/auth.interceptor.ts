import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

// Interceptor que adiciona Authorization: Bearer <token> a todas as requisições
// (exceto endpoints de auth) e trata 401 limpando o token e redirecionando para login.
export const authInterceptor: HttpInterceptorFn = (req, next) => {
	const token = localStorage.getItem('authToken');
	const isAuthEndpoint = /\/api\/auth\//.test(req.url);

	const reqWithAuth = token && !isAuthEndpoint && !req.headers.has('Authorization')
		? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
		: req;

	return next(reqWithAuth).pipe(
		catchError((error: HttpErrorResponse) => {
			if (error.status === 401) {
				localStorage.removeItem('authToken');
				try {
					const router = inject(Router);
					router.navigate(['/api/login']);
				} catch {}
			}
			return throwError(() => error);
		})
	);
};

