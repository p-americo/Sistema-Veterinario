import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { StorageService } from '../services/storage.service';

// Interceptor que adiciona Authorization: Bearer <token> a todas as requisições
// (exceto endpoints de auth).
export const authInterceptor: HttpInterceptorFn = (req, next) => {
	const storageService = inject(StorageService);
	const token = storageService.getItem('authToken');
	const isAuthEndpoint = /\/api\/auth\//.test(req.url);

	const reqWithAuth = token && !isAuthEndpoint && !req.headers.has('Authorization')
		? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
		: req;

	return next(reqWithAuth);
};
