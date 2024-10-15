using ASOS_Frontend;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.AspNetCore.Components.WebAssembly.Hosting;
using Microsoft.AspNetCore.Components.Authorization;
using MudBlazor;
using MudBlazor.Services;
using Radzen;
using Blazored.LocalStorage;
using System.Security.Claims;
using Services;
using Interfaces;

var builder = WebAssemblyHostBuilder.CreateDefault(args);
builder.RootComponents.Add<App>("#app");
builder.RootComponents.Add<HeadOutlet>("head::after");

builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddScoped<AuthDelegateHandler>();
builder.Services.AddHttpClient("API", client =>
{
	client.BaseAddress = new Uri("http://localhost:8080/");
})
.AddHttpMessageHandler<AuthDelegateHandler>();

//builder.Services.AddSingleton(sp => sp.GetRequiredService<IHttpClientFactory>().CreateClient("API"));

builder.Services.AddBlazoredLocalStorage();
builder.Services.AddAuthenticationCore();
builder.Services.AddAuthorizationCore();
builder.Services.AddScoped<AuthenticationStateProvider, JwtAuthStateProvider>();

builder.Services.AddRadzenComponents();

builder.Services.AddMudServices(config =>
{
	config.SnackbarConfiguration.PositionClass = Defaults.Classes.Position.TopCenter;
	config.SnackbarConfiguration.PreventDuplicates = false;
	config.SnackbarConfiguration.NewestOnTop = true;
	config.SnackbarConfiguration.ShowCloseIcon = true;
	config.SnackbarConfiguration.VisibleStateDuration = 2500;
	config.SnackbarConfiguration.HideTransitionDuration = 500;
	config.SnackbarConfiguration.ShowTransitionDuration = 500;
	config.SnackbarConfiguration.SnackbarVariant = MudBlazor.Variant.Filled;
	config.SnackbarConfiguration.MaximumOpacity = 100;
	config.SnackbarConfiguration.ClearAfterNavigation = false;
});

builder.Services.AddAuthorizationCore(config =>
{
	config.AddPolicy("StudentOnly", policy =>
		policy.RequireClaim(ClaimTypes.Role, "ROLE_STUDENT"));
	config.AddPolicy("TeacherOnly", policy =>
		policy.RequireClaim(ClaimTypes.Role, "ROLE_TEACHER"));
	config.AddPolicy("AdminOnly", policy =>
		policy.RequireClaim(ClaimTypes.Role, "ROLE_ADMIN"));
});

await builder.Build().RunAsync();