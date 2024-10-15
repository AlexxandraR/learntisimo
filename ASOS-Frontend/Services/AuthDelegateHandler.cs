using System.Net;
using System.Net.Http.Headers;
using Interfaces;
using Microsoft.AspNetCore.Components;

namespace Services
{
    public class AuthDelegateHandler : DelegatingHandler
    {
        readonly IAuthService _authService;
        readonly NavigationManager _navigationManager;
        bool isRefreshingAuthState = false;

		public AuthDelegateHandler(IAuthService authService, NavigationManager navigationManager)
        {
           _authService = authService;
		    _navigationManager = navigationManager;
		}

        protected override async Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, CancellationToken cancellationToken)
        {
            string? jwtToken = null;

            if (!isRefreshingAuthState)
            {
                jwtToken = await AddJwtTokenToAuthHeader(request);
            }
            
            var response = await base.SendAsync(request, cancellationToken);

            if (response.StatusCode == HttpStatusCode.Forbidden && !string.IsNullOrEmpty(jwtToken) && !isRefreshingAuthState)
			{
                try
                {
                    isRefreshingAuthState = true;

                    await _authService.RefreshTokenAsync();

                    await AddJwtTokenToAuthHeader(request);

                    response = await base.SendAsync(request, cancellationToken);
                }
                catch (Exception)
                {
					await _authService.LogoutAsync();

					_navigationManager.NavigateTo("/login", forceLoad:true);
				}
                finally
                {
                    isRefreshingAuthState = false;
				}
			}

			return response;
        }

        async Task<string?> AddJwtTokenToAuthHeader(HttpRequestMessage request)
        {
            string? jwtToken = await _authService.GetJwtTokenAsync();

            if (!string.IsNullOrWhiteSpace(jwtToken))
            {
                request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", jwtToken);
            }

            return jwtToken;
        }
    }
}
