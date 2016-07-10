Given /^I login RapidReg with "(.*?)" account$/ do |account|
  login_page.login(test_account[account])
end

When /^I login RapidReg for the first time with "(.*?)" and "(.*?)" and "(.*?)"$/ do |username,password,url|
  login_page.loginAs(username,password,url)
end

When /^I re-login RapidReg with "(.*?)" and "(.*?)"$/ do |username,password|
  login_page.reLoginAs(username,password)
end

When /^I press "(.*?)" button$/ do |button|
  base_page.clickById(button)
end

Then /^I should see "(.*?)"$/ do |text|
  base_page.verifyPromptExist(text)
end

When /^I logout$/ do
  login_page.logout
end

And /^I should see current user is "(.*?)"$/ do |username|
  sleep 5   # must
  login_page.getCurrentUser.equal?("#{username}")     #TODO
end

And /^the organization is "(.*?)"$/ do |org|
  login_page.getUserOrganization.equal?("#{org}")     #TODO
end
