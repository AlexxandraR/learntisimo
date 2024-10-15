using System.ComponentModel.DataAnnotations;

namespace ValidationAttributes
{
    public class FutureDateTimeOnlyAttribute : ValidationAttribute
    {
        protected override ValidationResult? IsValid(object? value, ValidationContext validationContext)
        {
            if (value is DateTime dateValue)
            {
                if (dateValue > DateTime.Now)
                {
                    return ValidationResult.Success;
                }
                else
                {
                    // Include the property name in the validation result
                    var memberNames = new[] { validationContext.MemberName };
                    return new ValidationResult("Nie je možné zvoliť uplynulý termín.", memberNames);
                }
            }

            return ValidationResult.Success;
        }
    }
}
