/*
 * Copyright (c) 2015-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
'use strict';

/**
 * @ngdoc controller
 * @name account.profile.prefs.controller:DockerRegistryListController
 * @description This class is handling the controller for the docker registry's list
 * @author Oleksii Orel
 */
export class DockerRegistryListController {

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor($mdDialog, $document, cheProfile, cheNotification) {
    this.$mdDialog = $mdDialog;
    this.$document = $document;
    this.cheProfile = cheProfile;
    this.cheNotification = cheNotification;

    this.registries = cheProfile.getRegistries();
    this.isLoading = true;

    let promise = cheProfile.fetchPreferences();
    promise.then(() => {
      this.isLoading = false;
    }, (error) => {
      this.isLoading = false;
      this.cheNotification.showError(error.data && error.data.message ? error.data.message : 'Load registry failed.');
    });
  }

  /**
   * Clicked on the '+' button to add a docker registry. Show the dialog
   * @param  event - the $event
   */
  showAddRegistryDialog(event) {
    this.$mdDialog.show({
      targetEvent: event,
      bindToController: true,
      clickOutsideToClose: true,
      controller: 'AddRegistryController',
      controllerAs: 'addRegistryController',
      parent: angular.element(this.$document.body),
      templateUrl: 'app/account/profile/prefs/add-registry/add-registry.html'
    });
  }

  /**
   * Clicked on the '-' button to remove the registry. Show the dialog
   * @param  event - the $event
   * @param registry - the selected registry
   */
  removeRegistry(event, registry) {
    let confirm = this.$mdDialog.confirm()
      .title('Would you like to remove registry ' + registry.url + ' ?')
      .content('Please confirm for the registry removal.')
      .ariaLabel('Remove registry')
      .ok('Remove')
      .cancel('Cancel')
      .clickOutsideToClose(true)
      .targetEvent(event);
    this.$mdDialog.show(confirm).then(() => {
      this.isLoading = true;
      let promise = this.cheProfile.removeRegistry(registry.url);
      promise.then(() => {
        this.isLoading = false;
      }, (error) => {
        this.isLoading = false;
        this.cheNotification.showError(error.data && error.data.message ? error.data.message : 'Delete registry failed.');
      });
    });
  }

}
