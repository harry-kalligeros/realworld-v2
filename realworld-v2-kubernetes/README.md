Kubernetes resources and instructions
=====================================

1. Create a persistent volume having the same `spec.storageClassName` as the _postgres-pv-claim_ in the deployment, e.g. as follows:

    - Put the following in the file `pv-local.yaml` - source https://kubernetes.io/docs/concepts/storage/volumes/#local:

        ```yaml
        apiVersion: v1
        kind: PersistentVolume
        metadata:
          name: local-pv
        spec:
          capacity:
            storage: 1Gi
          volumeMode: Filesystem
          accessModes:
          - ReadWriteOnce
          persistentVolumeReclaimPolicy: Delete
          storageClassName: local-storage
          local:
            path: /mnt/kube-local-pv
          nodeAffinity:
            required:
              nodeSelectorTerms:
              - matchExpressions:
                - key: kubernetes.io/hostname
                  operator: In
                  values:
                  - udocker
        ```

    - Run:

        ```shell script
        kubectl apply -f pv-local.yaml
        ```

2. Create the file `deployment-postgres.yaml` (contents besides this README)

3. Create the file `kustomization.yaml`:

        ```yaml
        resources:
          - deployment-postgres.yaml
        ```

4. Run:

        ```shell script
        kubectl apply -k ./
        ```

Database
--------

The service named _rwlv2-postgres_ is exposed on port 32543 on the Kubernetes node.


Destroying
----------

```shell script
kubectl delete -k ./
kubectl delete -f pv-local.yaml
```