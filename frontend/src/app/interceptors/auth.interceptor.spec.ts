import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { authInterceptor } from './auth.interceptor';
import { errorInterceptor } from './error.interceptor';
import { StorageService } from '../services/storage.service';
import { SessionService } from '../services/session.service';

describe('Auth & Error Interceptors', () => {
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  let mockStorageService: jasmine.SpyObj<StorageService>;
  let mockSessionService: jasmine.SpyObj<SessionService>;

  beforeEach(() => {
    mockStorageService = jasmine.createSpyObj('StorageService', ['getItem', 'removeItem']);
    mockSessionService = jasmine.createSpyObj('SessionService', ['logout']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor, errorInterceptor])),
        provideHttpClientTesting(),
        { provide: StorageService, useValue: mockStorageService },
        { provide: SessionService, useValue: mockSessionService }
      ]
    });

    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should add Authorization header with token for non-auth endpoints', () => {
    mockStorageService.getItem.and.returnValue('my-jwt-token');

    httpClient.get('/api/agendamentos').subscribe();

    const req = httpTestingController.expectOne('/api/agendamentos');
    expect(req.request.headers.has('Authorization')).toBeTrue();
    expect(req.request.headers.get('Authorization')).toBe('Bearer my-jwt-token');
    req.flush({});
  });

  it('should NOT add Authorization header for auth endpoints', () => {
    mockStorageService.getItem.and.returnValue('my-jwt-token');

    httpClient.post('/api/auth/login', {}).subscribe();

    const req = httpTestingController.expectOne('/api/auth/login');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
  });

  it('should NOT add Authorization header if token is missing', () => {
    mockStorageService.getItem.and.returnValue(null);

    httpClient.get('/api/agendamentos').subscribe();

    const req = httpTestingController.expectOne('/api/agendamentos');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
  });

  it('should call sessionService.logout() on 401 Unauthorized', () => {
    httpClient.get('/api/agendamentos').subscribe({
      next: () => {},
      error: (error) => {
        expect(error.status).toBe(401);
      }
    });

    const req = httpTestingController.expectOne('/api/agendamentos');
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

    expect(mockSessionService.logout).toHaveBeenCalled();
  });

  it('should call sessionService.logout() on 403 Forbidden', () => {
    httpClient.get('/api/agendamentos').subscribe({
      next: () => {},
      error: (error) => {
        expect(error.status).toBe(403);
      }
    });

    const req = httpTestingController.expectOne('/api/agendamentos');
    req.flush('Forbidden', { status: 403, statusText: 'Forbidden' });

    expect(mockSessionService.logout).toHaveBeenCalled();
  });
});
