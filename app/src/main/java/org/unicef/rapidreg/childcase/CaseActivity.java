package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.view.BaseActivity;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;

import static org.unicef.rapidreg.service.CaseService.CaseValues;

public class CaseActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.inflateMenu(R.menu.toolbar_main);
        toolbar.setOnMenuItemClickListener(new CaseMenuItemListener());
        toolbar.setTitle("Cases");

        if (savedInstanceState == null) {
            changeFragmentTo(new CaseListFragment(), false);
            showAddButton();
        }
    }

    private class CaseMenuItemListener implements Toolbar.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.search:
                    changeFragmentTo(new CaseSearchFragment(), true);
                    return true;

                case R.id.add_case:
                    CaseValues.clear();

                    if (!CaseFormService.getInstance().isFormReady()) {
                        Toast.makeText(CaseActivity.this,
                                R.string.syncing_forms_text, Toast.LENGTH_LONG).show();
                        return true;
                    }

                    changeFragmentTo(new CaseRegisterWrapperFragment(), true);
                    showSaveButton();
                    return true;

                case R.id.save_case:
                    CaseService caseService = CaseService.getInstance();
                    caseService.saveOrUpdateCase(CaseValues.getValues());
                    changeFragmentTo(new CaseListFragment(), false);
                    showAddButton();
                    return true;

                default:
                    return false;
            }
        }
    }

    private void showAddButton() {
        Menu menu = toolbar.getMenu();
        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(false);
    }

    private void showSaveButton() {
        Menu menu = toolbar.getMenu();
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(true);
    }

    private void changeFragmentTo(Fragment fragment, boolean needToBack) {
        String name = fragment.getClass().getSimpleName();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (needToBack) {
            transaction.replace(R.id.fragment_content, fragment, name)
                    .addToBackStack(null)
                    .commit();
        } else {
            transaction.replace(R.id.fragment_content, fragment, name)
                    .commit();
        }

        resetBarButtonsIfNeeded();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        resetBarButtonsIfNeeded();
    }

    private void resetBarButtonsIfNeeded() {
        if (isListFragmentVisible()) {
            showAddButton();
        }
    }

    private boolean isListFragmentVisible() {
        Fragment listFragment = getSupportFragmentManager()
                .findFragmentByTag(CaseListFragment.class.getSimpleName());

        return listFragment != null && listFragment.isVisible();
    }
}
