import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginCredentials, LoginResponse } from '../models/auth.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
	private apiUrl = `${environment.apiUrl}/api/auth/login`;

	constructor(private http: HttpClient) {}

	login(credentials: LoginCredentials): Observable<LoginResponse> {
		return this.http.post<LoginResponse>(this.apiUrl, credentials);
	}
}

